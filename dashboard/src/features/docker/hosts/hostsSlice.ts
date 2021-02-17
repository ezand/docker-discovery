import { createSlice, PayloadAction } from '@reduxjs/toolkit'

import { AppThunk } from '../../../app/store'
import { fetchHosts } from '../../../api/dockerDiscovery'
import Host from './Host'

interface HostsState {
    hosts: Host[],
    error: string | undefined
}

const hostsSlice = createSlice({
    name: 'hosts',
    initialState: {
        hosts: [] as Host[],
        error: undefined
    } as HostsState,
    reducers: {
        addHost(state, action: PayloadAction<Host>) {
            return {...state, hosts: [...state.hosts, action.payload]}
        },
        removeHost(state, action: PayloadAction<string>) {
            return {...state, hosts: state.hosts.filter((host: Host) => {
                return host.id !== action.payload
            })}
        },
        fetchHostsSuccess(state, action: PayloadAction<Host[]>) {
            return {...state, hosts: action.payload || []}
        },
        fetchHostsFailed(state, action: PayloadAction<string>) {
            return {...state, error: action.payload}
        }
    }
})

export const { addHost, removeHost, fetchHostsSuccess, fetchHostsFailed } = hostsSlice.actions

export default hostsSlice.reducer;

export const fetchDockerHosts = (): AppThunk => async dispatch => {
    fetchHosts().then(
        hosts => dispatch(fetchHostsSuccess(hosts)),
        error => dispatch(fetchHostsFailed(error.toString()))
    )
}
