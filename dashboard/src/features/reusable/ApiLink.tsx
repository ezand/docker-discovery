import React, { useState } from 'react'
import { CLink, CTooltip } from '@coreui/react'
import CIcon from '@coreui/icons-react'

import JsonViewDialog from '../reusable/JsonViewDialog'

interface ApiLinkProps {
    uri: string
    children?: any
    color?: string
}

//const defaultComponent = <small className="text-muted">JSON</small>
const defaultComponent = <CIcon name="cil-code" color="white" />

const ApiLink = ({ color, uri, children = defaultComponent }: ApiLinkProps) => {
    const [modal, setModal] = useState(false)
    const [data, setData] = useState({})
    const [loading, setLoading] = useState(false)

    const toggleModal = () => {
        setModal(!modal);
    }

    const onClick = () => {
        setLoading(true)
        toggleModal()
        fetch(uri)
            .then(response => response.json())
            .then(json => {
                setData(json)
                setLoading(false)
            })
    }

    return (
        <div className="card-header-actions">
            <JsonViewDialog title={uri} show={modal} loading={loading} onClose={toggleModal} json={data} />
            <CTooltip content="Show JSON content">
                <CLink
                    style={{ color: color }}
                    onClick={onClick}
                    className="card-header-action">
                    {children}
                </CLink>
            </CTooltip>
        </div>
    )
}

export default ApiLink;
