import React from 'react';
import { CSpinner } from '@coreui/react'

interface FetchSpinnerProps {
    color?: string
    center?: boolean
    size?: string
    grow?: boolean
}

const FetchSpinner = ({ color = 'info', center = true, size = undefined, grow = true }: FetchSpinnerProps) => {
    const clazz = [center ? 'text-center' : undefined]
        .filter(x => x !== undefined)
        .join(' ')
    
    return (
        <div className={clazz}>
            <CSpinner size={size} color={color} grow={grow} />
        </div>
    )
}

export default FetchSpinner
