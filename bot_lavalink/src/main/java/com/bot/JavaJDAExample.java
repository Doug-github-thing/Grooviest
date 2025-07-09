package com.bot;

import dev.arbjerg.lavalink.client.*;
import dev.arbjerg.lavalink.client.event.EmittedEvent;
import dev.arbjerg.lavalink.client.event.StatsEvent;
import dev.arbjerg.lavalink.client.event.TrackStartEvent;
import dev.arbjerg.lavalink.client.loadbalancing.RegionGroup;
import dev.arbjerg.lavalink.client.loadbalancing.builtin.VoiceRegionPenaltyProvider;
import dev.arbjerg.lavalink.client.player.*;
import dev.arbjerg.lavalink.libraries.jda.JDAVoiceUpdateListener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Random;

public class JavaJDAExample extends ListenerAdapter {
    private final LavalinkClient client;

    public static void main(String[] args) throws InterruptedException {
        new JavaJDAExample();
    }

    public JavaJDAExample() throws InterruptedException {
        final var token = System.getenv("DISCORD_TOKEN");
        this.client = new LavalinkClient(Helpers.getUserIdFromToken(token));

        this.client.getLoadBalancer().addPenaltyProvider(new VoiceRegionPenaltyProvider());

        this.registerLavalinkListeners();
        this.registerLavalinkNodes();

        JDABuilder.createDefault(token)
                .setVoiceDispatchInterceptor(new JDAVoiceUpdateListener(this.client))
                .enableIntents(GatewayIntent.GUILD_VOICE_STATES)
                .enableCache(CacheFlag.VOICE_STATE)
                .addEventListeners(this)
                .build()
                .awaitReady();
    }

    private void registerLavalinkNodes() {
        List.of(
            /*client.addNode(
                "Testnode",
                URI.create("ws://localhost:2333"),
                "youshallnotpass",
                RegionGroup.EUROPE
            ),*/

            client.addNode(new NodeOptions.Builder().setName("LocalNode")
                .setServerUri(URI.create("ws://localhost:2333/"))
                .setPassword("youshallnotpass")
                .setRegionFilter(RegionGroup.US)
                .setHttpTimeout(5000L)
                .build()
            )
        ).forEach((node) -> {
            node.on(TrackStartEvent.class).subscribe((event) -> {
                final LavalinkNode node1 = event.getNode();

                System.out.printf(
                        "%s: track started: %s%n",
                        node1.getName(),
                        event.getTrack().getInfo()
                );
            });
        });
    }

    private void registerLavalinkListeners() {
        this.client.on(dev.arbjerg.lavalink.client.event.ReadyEvent.class).subscribe((event) -> {
            final LavalinkNode node = event.getNode();

            System.out.printf(
                    "Node '%s' is ready, session id is '%s'!%n",
                    node.getName(),
                    event.getSessionId()
            );
        });

        this.client.on(StatsEvent.class).subscribe((event) -> {
            final LavalinkNode node = event.getNode();

            System.out.printf(
                    "Node '%s' has stats, current players: %d/%d%n",
                    node.getName(),
                    event.getPlayingPlayers(),
                    event.getPlayers()
            );
        });

        this.client.on(EmittedEvent.class).subscribe((event) -> {            
            if (event instanceof TrackStartEvent) {
                System.out.println("Is a track start event!");
            }

            final var node = event.getNode();

            System.out.printf(
                    "Node '%s' emitted event: %s%n",
                    node.getName(),
                    event
            );
        });
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        System.out.println(event.getJDA().getSelfUser().getAsTag() + " is ready!");

        event.getJDA().updateCommands()
                .addCommands(
                    Commands.slash("custom-request", "Testing custom requests")
                            .addOption(
                                OptionType.STRING,
                                "endpoint",
                                "Endpoint of the command to send",
                                true
                            ),
                    Commands.slash("join", "Join the voice channel you are in."),
                    Commands.slash("leave", "Leaves the vc"),
                    Commands.slash("stop", "Stops the current track"),
                    Commands.slash("pause", "Pause or unpause the plauer"),
                    Commands.slash("play", "Play a song")
                            .addOption(
                                OptionType.STRING,
                                "identifier",
                                "The identifier of the song you want to play",
                                true
                            )
                )
                .queue();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        switch (event.getFullCommandName()) {
            case "join":
                joinHelper(event);
                break;
            case "stop":
                this.client.getOrCreateLink(event.getGuild().getIdLong())
                    .updatePlayer(
                        (update) -> update.setTrack(null).setPaused(false)
                    )
                    .subscribe((__) -> {
                        event.reply("Stopped the current track").queue();
                    });
                break;
            case "leave":
                event.getJDA().getDirectAudioController().disconnect(event.getGuild());
                event.reply("Leaving your channel!").queue();
                break;
            case "pause":
                this.client.getOrCreateLink(event.getGuild().getIdLong())
                        .getPlayer()
                        .flatMap((player) -> player.setPaused(!player.getPaused()))
                        .subscribe((player) -> {
                            event.reply("Player has been " + (player.getPaused() ? "paused" : "resumed") + "!").queue();
                        });
                break;
            case "play": {
                final Guild guild = event.getGuild();

                // We are already connected, go ahead and play
                if (guild.getSelfMember().getVoiceState().inAudioChannel()) {
                    event.deferReply(false).queue();
                } else {
                    // Connect to VC first
                    joinHelper(event);
                }

                final String identifier = event.getOption("identifier").getAsString();
                final long guildId = guild.getIdLong();
                final Link link = this.client.getOrCreateLink(guildId);
                
                link.loadItem("ytsearch:"+identifier).subscribe(new FunctionalLoadResultHandler(
                    // Track loaded
                    (trackLoad) -> {
                        System.out.println("track load called");

                        final Track track = trackLoad.getTrack();

                        // Inner class at the end of this file
                        final var userData = new MyUserData(event.getUser().getIdLong());

                        track.setUserData(userData);

                        link.createOrUpdatePlayer()
                            .setTrack(track)
                            .setVolume(35)
                            .subscribe((player) -> {
                                final Track playingTrack = player.getTrack();
                                final var trackTitle = playingTrack.getInfo().getTitle();
                                final MyUserData customData = playingTrack.getUserData(MyUserData.class);

                                event.getHook().sendMessage("Now playing: " + trackTitle + "\nRequested by: <@" + customData.requester() + '>').queue();
                            });
                    },
                    null, // playlist loaded
                    // search result loaded
                    (search) -> {
                        final List<Track> tracks = search.getTracks();

                        if (tracks.isEmpty()) {
                            event.getHook().sendMessage("No tracks found!").queue();
                            return;
                        }

                        final Track firstTrack = tracks.get(0);

                        // This is a different way of updating the player! Choose your preference!
                        // This method will also create a player if there is not one in the server yet
                        link.updatePlayer((update) -> update.setTrack(firstTrack).setVolume(35))
                            .subscribe((ignored) -> {
                                event.getHook().sendMessage("Now playing: " + firstTrack.getInfo().getTitle()).queue();
                            });
                    },
                    () -> {System.out.println("no match");}, // no matches
                    (fail) -> {System.out.println("load failed");} // load failed
                ));

                break;
            }
            case "custom-request": {
                final String endpoint = event.getOption("endpoint").getAsString();
                final Link link = this.client.getOrCreateLink(event.getGuild().getIdLong());

                link.getNode().customRequest(
                        (builder) -> builder.get().path(endpoint).header("Accept", "text/plain")
                ).subscribe((response) -> {
                    try (ResponseBody body = response.body()) {
                        final String bodyText = body.string();

                        event.reply("Response from custom endpoint: " + bodyText).queue();
                    } catch (IOException e) {
                        event.reply("Something went wrong! " + e.getMessage()).queue();
                    }
                });
                break;
            }
            default:
                event.reply("Unknown command").queue();
                break;
        }
    }

    // Makes sure that the bot is in a voice channel!
    private void joinHelper(SlashCommandInteractionEvent event) {
        final Member member = event.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if (memberVoiceState.inAudioChannel()) {
            event.getJDA().getDirectAudioController().connect(memberVoiceState.getChannel());
        }

        event.reply("Joining your channel!").queue();
    }

    record MyUserData(long requester) {}
}