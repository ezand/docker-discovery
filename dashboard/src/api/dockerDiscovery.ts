import { Details, Host } from '../features/docker/hosts/types'

const fetchHosts = async (): Promise<Host[]> => {
    return fetch('/api/docker')
        .then(response => response.json())
}

const fetchHostDetails = async (name: string): Promise<Details> => {
    return fetch('/api/docker/' + name)
        .then(response => response.json())
}

export {
    fetchHosts,
    fetchHostDetails,
}
