import React from 'react'
import { useSelector } from 'react-redux'

import { RootState } from '../../../app/rootReducer'
import FetchSpinner from '../../reusable/FetchSpinner'

const HostList = () => {
    const { hosts, loading } = useSelector((state: RootState) => state.hostList)

    return loading ? <FetchSpinner size='sm' /> : (
        <ul>
            {hosts.map(host => <li key={host.name}>{host.name}</li>)}
        </ul>
    )
}

export default HostList
