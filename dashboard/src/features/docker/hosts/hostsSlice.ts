import { createSlice, PayloadAction } from '@reduxjs/toolkit'

import { AppThunk } from '../../../app/store'
import { fetchHosts, fetchHostDetails } from '../../../api/dockerDiscovery'
import { Host, LoadingHostSuccess, LoadingHost, LoadingHostError, HostsState } from './types'

const initialState: HostsState = {
    hosts: [] as Host[],
    loadingHosts: false,
    error: undefined
}

const hostsSlice = createSlice({
    name: 'hostList',
    initialState: initialState,
    reducers: {
        addHost(state, action: PayloadAction<Host>) {
            return { ...state, hosts: [...state.hosts, action.payload] }
        },
        removeHost(state, action: PayloadAction<string>) {
            return {
                ...state, hosts: state.hosts.filter((host: Host) => {
                    return host.name !== action.payload
                })
            }
        },
        setLoadingHosts(state, action: PayloadAction<boolean>) {
            return { ...state, loading: action.payload }
        },
        fetchHostsSuccess(state, action: PayloadAction<Host[]>) {
            return { ...state, hosts: action.payload || [], loadingHosts: false }
        },
        fetchHostsFailed(state, action: PayloadAction<string>) {
            return { ...state, error: action.payload, loadingHosts: false }
        },
        setLoadingHostDetails(state, action: PayloadAction<LoadingHost>) {
            const { name, loading } = action.payload
            return {
                ...state, hosts: state.hosts.map(host => {
                    if (host.name === name) {
                        return { ...host, loadingDetails: loading }
                    } else return host
                })
            }
        },
        fetchHostDetailsSuccess(state, action: PayloadAction<LoadingHostSuccess>) {
            const { name, details } = action.payload
            return {
                ...state, hosts: state.hosts.map(host => {
                    if (host.name === name) {
                        return { ...host, details: details, loadingDetails: false }
                    } else return host
                })
            }
        },
        fetchHostDetailsFailed(state, action: PayloadAction<LoadingHostError>) {
            const { name, error } = action.payload
            return {
                ...state, hosts: state.hosts.map(host => {
                    if (host.name === name) {
                        return { ...host, error: error, loadingDetails: false }
                    } else return host
                })
            }
        }
    }
})

export const {
    addHost,
    removeHost,
    fetchHostsSuccess,
    fetchHostsFailed,
    setLoadingHosts,
    setLoadingHostDetails,
    fetchHostDetailsSuccess,
    fetchHostDetailsFailed,
 } = hostsSlice.actions

export default hostsSlice.reducer;

export const fetchDockerHosts = (): AppThunk => async dispatch => {
    dispatch(setLoadingHosts(true))
    fetchHosts().then(
        hosts => {
            Promise.resolve(dispatch(fetchHostsSuccess(hosts)))
                .then(() => hosts.forEach(host => {
                    Promise.resolve(dispatch(setLoadingHostDetails({name: host.name, loading: true})))
                }))
                .then(() => {
                    hosts.forEach(host => {
                        fetchHostDetails(host.name).then(
                            details => dispatch(fetchHostDetailsSuccess({name: host.name, details: details})),
                            error => dispatch(fetchHostDetailsFailed({name: host.name, error: error.toString()}))
                        )
                    })
                })
        },
        error => dispatch(fetchHostsFailed(error.toString()))
    )
}
