import discord
from discord.ext import commands
import logging

def run_bot(TOKEN):
    # Initialize globals
    bot = commands.Bot(command_prefix="-", intents=discord.Intents.default())
    logging.basicConfig(format="%(asctime)s - %(message)s", level=logging.INFO)


    # Runs on startup
    @bot.event
    async def on_ready():
        await connect()


    # Connects the bot to the default voice channel
    async def connect():
        logging.info("~~~~~~~~~~~~~Connecting~~~~~~~~~~~~~")

        # Search for the correct channel
        for guild in bot.guilds:
            if (guild.name == '\U0001d610\'\U0001d62e \U0001d62a\U0001d62f'):
                for channel in guild.channels:
                    if channel.name == "General Cannoli":
                        logging.info(f"Connected to voice channel {channel.name}")
                        await channel.connect()

    bot.run(TOKEN)
