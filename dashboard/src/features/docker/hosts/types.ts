export interface Host {
    id: string
}

export interface HostsState {
    hosts: Host[],
    loading: boolean,
    error: string | undefined,
}
