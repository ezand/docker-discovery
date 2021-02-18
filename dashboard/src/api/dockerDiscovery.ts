import { Host } from '../features/docker/hosts/types'

const fetchHosts = async (): Promise<Host[]> => {
    // TODO fetch external content
    return fetch('/api/docker')
        .then(response => response.json())
}

export {
    fetchHosts
}
