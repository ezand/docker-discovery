<p align="center">
  <img height="300" src="https://github.com/ezand/docker-discovery/raw/main/doc/logo.png">
</p>

![lint status](https://github.com/ezand/docker-discovery/workflows/lint/badge.svg)

<div align="center">
  <img height="40" src="https://raw.githubusercontent.com/ezand/docker-discovery/main/doc/clojure.svg" />
  &nbsp;&nbsp;<img height="40" src="https://github.com/ezand/docker-discovery/raw/main/doc/docker.png" />
  &nbsp;&nbsp;<img height="40" src="https://raw.githubusercontent.com/ezand/docker-discovery/main/doc/mqtt.svg" />
  &nbsp;&nbsp;<img height="40" src="https://github.com/ezand/docker-discovery/raw/main/doc/websockets.png" />
</div>

# Work in progress

## Features
* Run in [Docker](https://www.docker.com/)
* Run as standalone [Clojure](https://clojure.org/) app
* Fully configurable
* REST API
* Receive Docker [events](https://docs.docker.com/engine/reference/commandline/events/)
  * Through [WebSockets](https://en.wikipedia.org/wiki/WebSocket)
  * Through [MQTT](https://mqtt.org/)
* Start/stop Docker containers
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

### Bugs

...

### Any Other Sections
### That You Think
### Might be Useful

## License

Copyright © 2021 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
