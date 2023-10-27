async def cleanup_resources(stomp):
    if stomp is not None:
        await stomp.close()
