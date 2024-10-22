def handler(event, context):
    cloudfront_event = event['Records'][0]['cf']
    request = cloudfront_event.get("request", None)
    response = cloudfront_event.get("response", None)

    # translate uri from /api to /...
    translated_uri = request['uri'].replace("/api", "")
    request['uri'] = translated_uri

    return response if (response is not None) else request
