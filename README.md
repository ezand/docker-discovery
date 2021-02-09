<p align="center">
  <img height="300" src="https://github.com/ezand/docker-discovery/raw/main/doc/logo.png">
</p>

![lint status](https://github.com/ezand/docker-discovery/workflows/lint/badge.svg)
![build status](https://github.com/ezand/docker-discovery/workflows/build/badge.svg)
![dep-check status](https://github.com/ezand/docker-discovery/workflows/dep-check/badge.svg)
![GitHub](https://img.shields.io/github/license/ezand/docker-discovery)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/ezand/docker-discovery)

<div align="center">
  <a target="_blank" href="https://clojure.org/"><img height="40" src="https://raw.githubusercontent.com/ezand/docker-discovery/main/doc/clojure.svg" /></a>
  &nbsp;&nbsp;<a target="_blank" href="https://www.docker.com/"><img height="40" src="https://github.com/ezand/docker-discovery/raw/main/doc/docker.png" /></a>
  &nbsp;&nbsp;<a target="_blank" href="https://mqtt.org/"><img height="40" src="https://raw.githubusercontent.com/ezand/docker-discovery/main/doc/mqtt.svg" /></a>
  &nbsp;&nbsp;<a target="_blank" href="https://en.wikipedia.org/wiki/WebSocket"><img height="40" src="https://github.com/ezand/docker-discovery/raw/main/doc/websockets.png" /></a>
  &nbsp;&nbsp;<a target="_blank" href="https://www.home-assistant.io/docs/mqtt/discovery/"><img height="40" src="https://upload.wikimedia.org/wikipedia/commons/6/6e/Home_Assistant_Logo.svg" /></a>
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
* Docker container attributes available as entity attributes in HomeAssistant.

## Installation

## Configuration

### Environment variables
| Name | Default | Description |
|------|---------|-------------|
| `LOG_LEVEL` | `debug` | One of `info,debug,trace,error,warn` |
| `CONFIG_FILE` | `/etc/docker-discovery/config.edn` | |
| `DOCKER_API_VERSION` | `v1.40` | |
| `DOCKER_EXPOSURE` | `mqtt` | Comma-separated list of `mqtt,websocket,rest` |
| `HTTP_PORT` | `3000` | |
| `HTTP_USERNAME` | | |
| `HTTP_PASSWORD` | | |
| `MQTT_URI` | | |
| `MQTT_USERNAME` | | |
| `MQTT_PASSWORD` | | |
| `MQTT_REFRESH` | `3600` | Refresh interval in seconds |
| `MQTT_PLATFORMS` | | `homeassistant` is the only supported platform atm. |
| `WEBSOCKET_REFRESH` | `3600` | Refresh interval in seconds |
| `HOST_<NAME>_URI` | | |
| `HOST_<NAME>_EVENTS` | `true` | `false` will disable listeing for events for this particular host. |
| `HOST_<NAME>_USERNAME` | | |
| `HOST_<NAME>_PASSWORD` | | |

## Security

To enable `basic authentication` on the REST endpoints, set the 
`http.username` and `http.password` properties. If you don't need the REST API,
I recommend disabling it by omitting it from the `docker-exposure` property.

### SSL

Docker Discovery doesn't provide a built-in solution for HTTPS or WSS. I recommend
placing something like Nginx in front to provide that functionality. I can recommend
[Nginx Proxy Manager](https://nginxproxymanager.com/) 👍 With that you can also handle
authentication (`basic auth`) in the proxy manager instead, it gives more flexibility.

## MQTT

### HomeAssistant

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

## Future plans
* Use [component](https://github.com/stuartsierra/component) to manage lifecycle.
* Make more of the features configurable.
* More Docker information available through REST API, without duplicating the original Docker API.
* Make WebSockets more secure (?). Some kind of credentials (?)
* Handle more Docker events.
* Support other MQTT platforms than HomeAssistant (?)
* Create a custom HomeAssistant integration making use of the WebSockets event. This will be a separate repo of course.
* Create a custom HomeAssistant Lovelace card displaying more of the Docker container attributes etc. Also a separate repo.

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

<a href="https://www.buymeacoffee.com/ezand" target="_blank"><img height="40" src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me A Coffee" /></a>
