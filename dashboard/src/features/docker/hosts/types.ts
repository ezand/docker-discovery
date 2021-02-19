export interface Details {
    id: string
}
export interface Host {
    name: string
    local: boolean
    loadingDetails?: boolean
    details?: Details
    error?: string
}
export interface LoadingHost {
    name: string
    loading: boolean
}

export interface LoadingHostSuccess {
    name: string
    details: Details
}

export interface LoadingHostError {
    name: string
    error: string
}

export interface HostsState {
    hosts: Host[]
    loadingHosts: boolean
    error?: string
}
