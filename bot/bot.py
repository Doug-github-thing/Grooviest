import discord
from discord.ext import commands
import logging

class Bot(discord.Client):

    logging.basicConfig(format="%(asctime)s - %(message)s", level=logging.INFO)

    def __init__(self):
        self.bot = commands.Bot(command_prefix="-", intents=discord.Intents.default())
        self._connection = self.bot._connection

        # Runs on startup
        @self.bot.event
        async def on_ready():
            logging.info(f"Logged in as {self.bot.user.name}")
            logging.info(f"Connection: {self.bot._connection}")
            logging.info(f"Guilds: {self.bot._connection.guilds}")

        @self.bot.command(name='ping')
        async def ping(ctx):
            await ctx.send('PONG')

    # Connects the bot to the default voice channel
    async def join(self):
        logging.info("~~~~~~~~~~~~~Connecting To Voice~~~~~~~~~~~~~")
        logging.info(f"My Connection object: {self.bot._connection}")
        logging.info(f"My connected guilds: {self.bot._connection.guilds}")

        # Search for the correct channel
        for guild in self.bot.guilds:
            if (guild.name == '\U0001d610\'\U0001d62e \U0001d62a\U0001d62f'):

                logging.info("Found I'm In")

                for channel in  guild.channels:
                    if channel.name == "General Cannoli":
                        logging.info(f"Connecting to voice channel {channel.name}")
                        await channel.connect()

        # await self.bot.close()
        

    # Run the bot
    def begin(self, TOKEN):
        self.bot.run(TOKEN)
