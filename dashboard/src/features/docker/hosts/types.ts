export interface Host {
    id: string
}

export interface HostsState {
    hosts: Host[],
    error: string | undefined
}
