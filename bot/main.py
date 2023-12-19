# Core bot function
import bot

# Globals
import os
from dotenv import load_dotenv 
import logging


# Initialize globals
load_dotenv()
TOKEN = os.getenv("DISCORD_TOKEN")
logging.basicConfig(format="%(asctime)s - %(message)s", level=logging.INFO)


if(__name__=="__main__"):
    bot.run_bot(TOKEN)
