import asyncio, websockets

from app.stomp_client.utils import cleanup_resources
from app.core.logger_config import get_logger

logger = get_logger(__name__)


async def keep_alive(app, broker_url):
    sec = app.state.keep_alive_interval
    while True:  # while not stomp.open: 으로 바꿔도 되려나
        stomp = app.state.stomp
        if stomp is None or not stomp.open:
            logger.warning("[Ping] Stomp 연결이 끊어졌습니다. 재연결을 시도합니다.")
            await stomp_client(app, broker_url)
        else:
            logger.info("[Ping] Stomp가 연결 되어있습니다.")
            await asyncio.sleep(sec)


async def stomp_client(app, broker_url):
    sec = app.state.reconnect_delay
    while app.state.stomp is None or not app.state.stomp.open:
        try:
            stomp = await websockets.connect(broker_url, max_size=2**30)
            connect_message = (
                "CONNECT\n"
                "accept-version:1.1,1.0\n"
                "heart-beat:10000,10000\n"
                "Authorization:python-emotion\n"
                "\n\0"
            )
            await stomp.send(connect_message)

            response = await stomp.recv()
            logger.info(response)
            app.state.stomp = stomp
            break
        except (OSError, websockets.exceptions.WebSocketException) as e:
            await cleanup_resources(app.state.stomp)
            logger.error(f"자바 서버와 Stomp 연결에 실패했습니다. {sec}초 후에 재시도 합니다... , {str(e)}")
            await asyncio.sleep(sec)
