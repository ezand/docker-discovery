<p align="center">
  <img height="300" src="https://github.com/ezand/docker-discovery/raw/main/doc/logo.png">
</p>

![lint status](https://github.com/ezand/docker-discovery/workflows/lint/badge.svg)

<div align="center">
  <img height="40" src="https://raw.githubusercontent.com/ezand/docker-discovery/main/doc/clojure.svg" />
  &nbsp;&nbsp;<img height="40" src="https://github.com/ezand/docker-discovery/raw/main/doc/docker.png" />
  &nbsp;&nbsp;<img height="40" src="https://raw.githubusercontent.com/ezand/docker-discovery/main/doc/mqtt.svg" />
  &nbsp;&nbsp;<img height="40" src="https://github.com/ezand/docker-discovery/raw/main/doc/websockets.png" />
  &nbsp;&nbsp;<img height="40" src="https://raw.githubusercontent.com/ezand/docker-discovery/main/doc/homeassistant.svg" />
</div>

# Work in progress

## Features
* Run in [Docker](https://www.docker.com/)
* Run as standalone [Clojure](https://clojure.org/) app
* Supports [local](https://stackoverflow.com/a/40007244/655296) or [remote Docker API](https://blog.usejournal.com/how-to-enable-docker-remote-api-on-docker-host-7b73bd3278c6)
* Fully configurable
  * Configure multiple Docker hosts
  * Disable / enable many aspect of the discovery
  * Supports config-file or environment variables configuration
* REST API
* Receive Docker [events](https://docs.docker.com/engine/reference/commandline/events/)
  * Through [WebSockets](https://en.wikipedia.org/wiki/WebSocket)
  * Through [MQTT](https://mqtt.org/)
* Start / stop Docker containers
* [HomeAssistant MQTT discovery](https://www.home-assistant.io/docs/mqtt/discovery/) compatible

## Installation

## Configuration

### Environment variables
| Name | Default | Example |
|------|---------|---------|
| `LOG_LEVEL` | | |
| `CONFIG_FILE` | | |
| `DOCKER_API_VERSION` | | |
| `DOCKER_EXPOSURE` | | |
| `HTTP_PORT` | | |
| `HTTP_USERNAME` | | |
| `HTTP_PASSWORD` | | |
| `MQTT_URI` | | |
| `MQTT_USERNAME` | | |
| `MQTT_PASSWORD` | | |
| `MQTT_REFRESH` | | |
| `WEBSOCKET_REFRESH` | | |
| `DOCKER_<HOST>_URI` | | |
| `DOCKER_<HOST>_EVENTS` | | |
| `DOCKER_<HOST>_USERNAME` | | |
| `DOCKER_<HOST>_PASSWORD` | | |

## HomeAssistant

## WebSockets

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

## Examples
...

## License

```
MIT License

Copyright (c) 2021 Eirik Stenersen Sand

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## Bugs

...
