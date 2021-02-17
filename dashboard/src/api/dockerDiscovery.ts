import Host from '../features/docker/hosts/Host'

const fetchHosts = async (): Promise<Host[]> => {
    return Promise.resolve([{id: "test-123"}, {id: "test-321"}])
}

export {
    fetchHosts
}
