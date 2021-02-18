import { createSlice, PayloadAction } from '@reduxjs/toolkit'

import { AppThunk } from '../../../app/store'
import { fetchHosts } from '../../../api/dockerDiscovery'
import { Host, HostsState } from './types'

const hostsSlice = createSlice({
    name: 'hostList',
    initialState: {
        hosts: [] as Host[],
        loading: false,
        error: undefined
    } as HostsState,
    reducers: {
        addHost(state, action: PayloadAction<Host>) {
            return {...state, hosts: [...state.hosts, action.payload]}
        },
        removeHost(state, action: PayloadAction<string>) {
            return {...state, hosts: state.hosts.filter((host: Host) => {
                return host.name !== action.payload
            })}
        },
        fetchHostsSuccess(state, action: PayloadAction<Host[]>) {
            return {...state, hosts: action.payload || [], loading: false}
        },
        fetchHostsFailed(state, action: PayloadAction<string>) {
            return {...state, error: action.payload, loading: false}
        },
        setLoading(state, action: PayloadAction<boolean>) {
            return {...state, loading: action.payload}
        }
    }
})

export const { addHost, removeHost, fetchHostsSuccess, fetchHostsFailed, setLoading } = hostsSlice.actions

export default hostsSlice.reducer;

export const fetchDockerHosts = (): AppThunk => async dispatch => {
    dispatch(setLoading(true))
    fetchHosts().then(
        hosts => dispatch(fetchHostsSuccess(hosts)),
        error => dispatch(fetchHostsFailed(error.toString()))
    )
}
