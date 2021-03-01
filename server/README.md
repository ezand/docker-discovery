# Docker Discovery - Server

<a target="_blank" href="https://clojure.org/"><img height="40" src="https://raw.githubusercontent.com/ezand/docker-discovery/main/doc/clojure.svg" /></a>
<a target="_blank" href="https://leiningen.org/"><img height="40" src="https://purelyfunctional.tv/wp-content/uploads/2019/07/leiningen-logo.png" /></a>

Docker Discovery server is written in [Clojure](https://clojure.org/) and uses [Leiningen](https://leiningen.org/) as build tool.

## Commands

| Command | Description |
|---------|-------------|
| `lein run` | Start the server. Runs the specified `main`. |
| `lein repl` | Start a REPL with correct classpath. |
| `lein build` | Creates an [überjar](https://github.com/technomancy/leiningen/blob/master/doc/TUTORIAL.md#uberjar). |
| `lein t` | Run tests. |
| `lein l` | Run lint using [clj-kondo](https://github.com/clj-kondo/clj-kondo). |
| `lein d` | Check for outdated dependencies using [lein-ancient](https://github.com/xsc/lein-ancient). |
| `lein license update` | Update license files with recent year etc. Uses [lein-license](https://github.com/xsc/lein-license). |

## Properties

| Property | edn | env | Default |
|----------|-----|-----|-------------|
| Config file | _N/A_ | `CONFIG_FILE` | `/etc/docker-discovery/config.edn` |
| Log level | `:log-level` | `LOG_LEVEL` | `:debug`<br/>`"debug"` |
| Docker API version | `:docker.api-version` | `DOCKER_API_VERSION` | `"v1.40"`|
| Enabled services | `:docker.exposure` | `DOCKER_EXPOSURE` | `#{:mqtt :websocket :rest}`<br/>`"mqtt,webscoket,rest"` |
| Docker host URI | `:docker.hosts.<unique_name>.uri` | `HOST_<NAME>_URI` | |
| Docker host events enabled | `:docker.hosts.<unique_name>.events` | `HOST_<NAME>_EVENTS` | `true` |
| Docker host username | `:docker.hosts.<unique_name>.username` | `HOST_<NAME>_USERNAME` | |
| Docker host username | `:docker.hosts.<unique_name>.password` | `HOST_<NAME>_PASSWORD` | |
| Web server port | `:http.port` | `HTTP_PORT` | `4000` |
| Web server username | `:http.username` | `HTTP_USERNAME` | |
| Web server password | `:http.password` | `HTTP_PASSWORD` | |
| Websocket refresh interval | `:websocket.refresh` | `WEBSOCKET_REFRESH` | `3600` ms |
| MQTT URI | `:mqtt.uri` | `MQTT_URI` | |
| MQTT username | `:mqtt.username` | `MQTT_USERNAME` | |
| MQTT password | `:mqtt.password` | `MQTT_PASSWORD` | |
| MQTT platforms | `:mqtt.platforms` | `MQTT_PLATFORMS` | `#{:homeassistant}`<br/>"homeassistant" |
| MQTT refresh interval | `:mqtt.refresh` | `MQTT_REFRESH` | `3600` ms |

## Logging

Logging uses SLF4J and goes to stout by default. Default log-level is `debug`. \
Log level can be configured by using one of these values:

__EDN__: `:info` `:debug` `:error` `:warn` `:trace` \
__ENV__: `"info"` `"debug"` `"error"` `"warn"` `"trace"`

## Web server

The REST api is availble on port `4000` by default. To protect the REST api using basic authentication,
specify the http username and -password properties.

The REST api will let you retrieve information about the configured Docker hosts
and their containers.

## Websockets

A websocket connection can be established from `ws://<host>:<port>/ws`.\
The websocket api will let you get the initial state of all Docker containers,
as well getting updates whenever a Docker event occurs. You can also specify a 
refresh interval for when to do a full refresh of the Docker container's state.

You will also be able to `start`/`stop` containers.

If you've set up basic authentication for the web server, the websocket communication
needs to contain correct username and password.

## MQTT

The MQTT feature will make publish container states to MQTT. The only supported platform
at the moment is Home Assistant. Home Assistant will pick up the configured
Docker hosts as `Devices`, and the containers will be `Entities` attached
to their respective `Device`.

Docker events will trigger messages being published to MQTT, thus updating the
entities in Home Asssitant. You can also specify a refresh interval for when to 
do a full refresh of the Docker container's state.

## Example EDN
```clojure
{:log-level :debug
 :docker {:api-version "v1.40"
          :exposure #{:websocket :rest :mqtt}
          :hosts {:myhost {:uri "///var/run/docker.sock"
                           :events true}
                  :otherhost {:uri "tcp://192.168.0.1:2375"
                              :events false
                              :username "user"
                              :password "passwd"}}}
 :http {:port 4000
        :username "user"
        :password "pass"}
 :websocket {:refresh 3600}
 :mqtt {:uri "tcp://192.168.0.2:1883"
        :refresh 300
        :username "user"
        :password "pass"
        :platforms #{:homeassistant}}}
```

## Example environment properties
```bash
CONFIG_FILE=/custom/location/my-config.edn
LOG_LEVEL=trace

HOST_MYHOST_URI=unix:///var/run/docker.sock
HOST_MYHOST_EVENTS=false
HOST_OTHERHOST_URI=tcp://192.168.144.5:2375
HOST_OTHERHOST_USERNAME=user
HOST_OTHERHOST_PASSWORD=passwd

DOCKER_EXPOSURE=mqtt,websocket,rest

HTTP_PORT=4000
HTTP_USERNAME=user
HTTP_PASSWORD=passwd
HTTP_SSL_PORT=9443

MQTT_URI=tcp://192.168.144.2:1883
MQTT_USERNAME=user
MQTT_PASSWORD=passwd
MQTT_REFRESH=2600
MQTT_PLATFORMS=homeassistant

WEBSOCKET_REFRESH=3600
```
