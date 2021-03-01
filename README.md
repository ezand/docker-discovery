<p align="center" style="display: flex; align-items: center">
  <img height="200" src="https://www.docker.com/sites/default/files/d8/2019-07/Moby-logo.png">
  <h1 style="font-size: 34pt; padding-right: 20px">
    Docker Discovery
  </h1>
</p>

![lint status](https://github.com/ezand/docker-discovery/workflows/server-lint/badge.svg)
![build status](https://github.com/ezand/docker-discovery/workflows/server-build/badge.svg)
![dep-check status](https://github.com/ezand/docker-discovery/workflows/server-dep-check/badge.svg)
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

__TODO__

### Docker

```bash
docker run......
```

### Docker compose

```yaml
version: "3.8"
```

## Configuration

You can either configure the app using an [edn](https://github.com/edn-format/edn) file,
or by specifying environment variables.

### Multi-value properties

__EDN__: Keyword-values inside a `set`, ex.: `#{:mqtt :rest}`

__Environment properties__: Comma-separated string-value, ex.: `"mqtt,rest"`

### Properties
See the server [README](./server/README.md)

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

### Functional enhancements

* Make more of the features configurable.
* More Docker information available through REST API, without duplicating the original Docker API.
* Handle more Docker events.
* Support other MQTT platforms than HomeAssistant (?)
* Create a custom HomeAssistant integration making use of the WebSockets event. This will be a separate repo of course.
* Create a custom HomeAssistant Lovelace card displaying more of the Docker container attributes etc. Also a separate repo.

### Technical improvements

* Use [component](https://github.com/stuartsierra/component) to manage application lifecycle.
* Use [reitit](https://github.com/metosin/reitit) as router.

## Keywords

[Docker](https://www.docker.com), 
[Home Assistant](https://www.home-assistant.io/docs/mqtt/discovery),
[Clojure](https://clojure.org),
[Websockets](https://en.wikipedia.org/wiki/WebSocket),
[MQTT](https://mqtt.org)

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
