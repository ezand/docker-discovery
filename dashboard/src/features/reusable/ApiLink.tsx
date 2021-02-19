import React, { useState } from 'react'
import { CLink } from '@coreui/react'

import JsonViewDialog from '../reusable/JsonViewDialog'

interface ApiLinkProps {
    uri: string
    altText?: string
    children?: any
}

const defaultComponent = <small className="text-muted">JSON</small>
const defaultAltText = "Show JSON"

const ApiLink = ({ uri, altText = defaultAltText, children = defaultComponent }: ApiLinkProps) => {
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
            <CLink
                alt={altText}
                onClick={onClick}
                target="_blank"
                className="card-header-action">
                {children}
            </CLink>
        </div>
    )
}

export default ApiLink;
