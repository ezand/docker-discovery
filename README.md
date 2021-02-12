<p align="center">
  <img height="300" src="https://github.com/ezand/docker-discovery/raw/main/doc/logo.png">
</p>

![lint status](https://github.com/ezand/docker-discovery/workflows/lint/badge.svg)
![build status](https://github.com/ezand/docker-discovery/workflows/build/badge.svg)
![dep-check status](https://github.com/ezand/docker-discovery/workflows/dep-check/badge.svg)
![GitHub](https://img.shields.io/github/license/ezand/docker-discovery)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/ezand/docker-discovery)

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

You can either configure the app using an [edn](https://github.com/edn-format/edn) file,
or by specifying environment variables.

### Multi-value properties

__EDN__: Keyword-values inside a `set`, ex.: `#{:mqtt :rest}` 

__Environment properties__: Comma-separated string-value, ex.: `"mqtt,rest"`

### Properties

| Property _(edn and env vars)_ | Default | Possible values | Multi-value? |
|-------------------------------|---------|-----------------|:------------:| 
| `CONFIG_FILE` | `/etc/docker-discovery/config.edn` | | |
| `:log-level`<br/>`LOG_LEVEL` | `:debug`<br/>`"debug"` | `:debug` `:info` `:trace` `:warn` `:error`<br/>`"debug"` `"info"` `"trace"` `"warn"` `"error"` | |
| `:docker.api-version`<br/>`DOCKER_API_VERSION` | `"v1.40"` | | |
| `:docker.exposure`<br/>`DOCKER_EXPOSURE` | `#{:mqtt}`<br/>`"mqtt"` | `:mqtt` `:websocket` `rest`<br/>`"mqtt"` `"websocket"` `"rest"` | ☑️ |
| `:http.port`<br/>`HTTP_PORT` | `3000` | | |
| `:http.username`<br/>`HTTP_USERNAME` | | | |
| `:http.password`<br/>`HTTP_PASSWORD` | | | |
| `:mqtt.uri`<br/>`MQTT_URI` | | | |
| `:mqtt.username`<br/>`MQTT_USERNAME` | | | |
| `:mqtt.password`<br/>`MQTT_PASSWORD` | | | |
| `:mqtt.refresh`<br/>`MQTT_REFRESH` | `3600` (seconds) | | |
| `:mqtt.platforms`<br/>`MQTT_PLATFORMS` | | `:homeassistant`<br/>`"homeassistant"` | ☑️ |
| `:websocket.refresh`<br/>`WEBSOCKET_REFRESH` | `3600` (seconds) | | |
| `:docker.host.<name>.uri`<br/>`HOST_<NAME>_URI` | | | |
| `:docker.host.<name>.events`<br/>`HOST_<NAME>_EVENTS` | `true` | | |
| `:docker.host.<name>.username`<br/>`HOST_<NAME>_USERNAME` | | | |
| `:docker.host.<name>.password`<br/>`HOST_<NAME>_PASSWORD` | | | |

### Example EDN file

```clojure
{:log-level :debug
 :docker {:api-version "v1.40"
          :exposure #{:websocket :rest :mqtt}
          :hosts {:local {:uri "///var/run/docker.sock"
                          :events true}
                  :remote {:uri "tcp://192.168.0.1:2375"
                           :events false
                           :username "user"
                           :password "passwd"}}}
 :http {:port 3000
        :username "user"
        :password "pass"}
 :websocket {:refresh 3600}
 :mqtt {:uri "tcp://192.168.0.2:1883"
        :refresh 300
        :username "user"
        :password "pass"
        :platforms #{:homeassistant}}}
```

## Security

To enable `basic authentication` on the REST and WebSocket endpoints, set the 
`http.username` and `http.password` properties. If you don't need the REST API,
I recommend disabling it by omitting it from the `docker.exposure` property.

### SSL

Docker Discovery doesn't provide a built-in solution for HTTPS or WSS. I recommend
placing something like Nginx in front to provide that functionality. I can recommend
[Nginx Proxy Manager](https://nginxproxymanager.com/) 👍 With that you can also handle
authentication (`basic auth`) in the proxy manager instead, it gives more flexibility.

## MQTT

Set the MQTT username and password properties if your broker requires authentication.

### HomeAssistant

When enabled, Docker Discovery will publish HomeAssistant supported messages to MQTT.

Each Docker host will become a `device` and the containers will become `switches` for that respective device.

The switches will have some container attributes available on the `entity`.

## WebSockets

You can find the websocket api [here](doc/websocket-api.md).

## Future plans
* Make more of the features configurable.
* More Docker information available through REST API, without duplicating the original Docker API.
* Handle more Docker events.
* Support other MQTT platforms than HomeAssistant (?)
* Use [component](https://github.com/stuartsierra/component) to manage application lifecycle.
* Create a custom HomeAssistant integration making use of the WebSockets event. This will be a separate repo of course.
* Create a custom HomeAssistant Lovelace card displaying more of the Docker container attributes etc. Also a separate repo.

## Technologies

<a target="_blank" href="https://clojure.org/"><img height="40" src="https://raw.githubusercontent.com/ezand/docker-discovery/main/doc/clojure.svg" /></a>
&nbsp;&nbsp;<a target="_blank" href="https://www.docker.com/"><img height="40" src="https://github.com/ezand/docker-discovery/raw/main/doc/docker.png" /></a>
&nbsp;&nbsp;<a target="_blank" href="https://mqtt.org/"><img height="40" src="https://raw.githubusercontent.com/ezand/docker-discovery/main/doc/mqtt.svg" /></a>
&nbsp;&nbsp;<a target="_blank" href="https://en.wikipedia.org/wiki/WebSocket"><img height="40" src="https://github.com/ezand/docker-discovery/raw/main/doc/websockets.png" /></a>
&nbsp;&nbsp;<a target="_blank" href="https://www.home-assistant.io/docs/mqtt/discovery/"><img height="40" src="https://upload.wikimedia.org/wikipedia/commons/6/6e/Home_Assistant_Logo.svg" /></a>

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
