import { Host } from '../features/docker/hosts/types'

const fetchHosts = async (): Promise<Host[]> => {
    // TODO fetch external content
    return Promise.resolve([{id: "test-123"}, {id: "test-321"}])
}

export {
    fetchHosts
}
