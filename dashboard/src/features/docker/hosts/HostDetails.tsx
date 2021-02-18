import React from 'react'
import { RouteComponentProps } from 'react-router-dom'

type TParams = { name: string }

const HostDetails = ({match}: RouteComponentProps<TParams>) => {
    const name = match.params.name

    return (
        <div>Host Details: {name}</div>
    )
}

export default HostDetails
