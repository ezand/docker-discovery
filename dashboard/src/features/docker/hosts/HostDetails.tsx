import React from 'react'
import { RouteComponentProps } from 'react-router-dom'

type TParams = { id: string }

const HostDetails = ({match}: RouteComponentProps<TParams>) => {
    const id = match.params.id

    return (
        <div>Host Details: {id}</div>
    )
}

export default HostDetails
