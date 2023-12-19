# For starting the bot
import os
from dotenv import load_dotenv
from bot import Bot

global bot
my_bot = Bot()

# For starting the server
from quart import Quart, request
import logging
logging.basicConfig(format="%(asctime)s - %(message)s", level=logging.INFO)


# Quart setup
app = Quart(__name__)

@app.route('/')
async def index():
    await my_bot.join()
    print("Receieved HTTP")
    return 'Hello, this is the web server!'

@app.route('/webhook', methods=['POST'])
async def webhook():
    data = await request.get_json()
    print(f'Received webhook data: {data}')
    # Perform bot actions based on the received data
    # You can customize this part based on your requirements
    return 'Webhook received successfully'


def start_bot():
    load_dotenv()
    TOKEN = os.getenv("DISCORD_TOKEN")
    my_bot.begin(TOKEN)

def start_web():
    app.run(port=5000)

if __name__ == '__main__':
    # Run web server in a separate Process
    from multiprocessing import Process # for multitrack drifting

    # Run Quart and Discord bot in separate processes
    quart_process = Process(target=start_web)
    bot_process = Process(target=start_bot)

    try:
        quart_process.start()
        bot_process.start()

        quart_process.join()
        bot_process.join()

    except KeyboardInterrupt:
        # Handle KeyboardInterrupt (Ctrl+C) and terminate the processes
        print("Terminating processes")
        bot_process.terminate()
        quart_process.terminate()
