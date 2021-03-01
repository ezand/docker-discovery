import React from 'react'
import { CBadge } from '@coreui/react'

interface DockerHostBadgeProps extends CBadge {
    local?: boolean
}

export const badgeColor = (local: boolean | undefined) => local ? "info" : "warning"
export const badgeText = (local: boolean | undefined) => local ? "LOCAL" : "REMOTE"

const DockerHostBadge = ({ local, ...rest }: DockerHostBadgeProps) => {
    const props = {...{ color: badgeColor(local) }, ...rest}

    return (
        <CBadge {...props}>
            {badgeText(local)}
        </CBadge>
    )
}

export default DockerHostBadge
