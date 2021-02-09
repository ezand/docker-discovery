# Work in progress ⚠️

### Start listening for Docker events

#### Request

```json
{
  "messageId": "some-id-123",
  "command": "start_listening",
  "events": {
    "container": {
      "start": true,
      "stop": true
    }
  }
}
```

#### Response

```json
{
  "messageId": "some-id-123",
  "command": "start_listening",
  "success": true,
  "result": {
    "state": {
      "hosts": [
        {
          "name": "docker-host-1",
          "attributes": {
            "manufacturer": "Docker Inc."
          },
          "containers": [
            {
              "id": "container-id-1",
              "name": "Docker Discovery",
              "attributes": {
                "state": "running",
                "status": "Up 35 hours"
              }
            }
          ]
        }
      ]
    }
  }
}
```

### Stop listening for Docker events

#### Request

```json
{
  "messageId": "some-id-321",
  "command": "stop_listening"
}
```

#### Response

```json
{
  "messageId": "some-id-321",
  "command": "stop_listening",
  "success": true
}
```