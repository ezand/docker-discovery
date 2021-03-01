export interface Details {
    id: string
    name: string
    version: string
    apiVersion: string
    containersPaused: number
    containersStopped: number
    containersRunning: number
    architecture: string
    iPv4forwarding: boolean
    experimentalBuild: boolean
    ping: string
    liveRestoreEnabled: boolean
    swapLimit: boolean
    images: number
    containers: number
    labels: string[]
    nEventsListener: number
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
