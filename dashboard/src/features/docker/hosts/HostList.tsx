import React from 'react';
import { useSelector } from 'react-redux'
import { RootState } from '../../../app/rootReducer'

const HostList = () => {
    const { hosts, loading } = useSelector((state: RootState) => state.hostList)

    const list = hosts.map(host => <li key={host.id}>{host.id}</li>)

    return (
        <>
            <div>Hosts</div>
            <ul>
                {list}
            </ul>
        </>
    )
}

export default HostList
