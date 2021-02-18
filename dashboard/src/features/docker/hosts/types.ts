export interface Host {
    name: string,
    local: boolean
}

export interface HostsState {
    hosts: Host[],
    loading: boolean,
    error: string | undefined,
}
