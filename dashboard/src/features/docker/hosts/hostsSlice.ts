import { createSlice, PayloadAction } from '@reduxjs/toolkit'
import Host from './Host'

const hostsSlice = createSlice({
    name: 'hosts',
    initialState: [] as Host[],
    reducers: {
        addHost(state, action: PayloadAction<Host>) {
            state.push(action.payload)
        },
        removeHost(state, action: PayloadAction<string>) {
            state = state.filter((host: Host) => {
                return host.id !== action.payload
            })
        }
    }
})

export const { addHost, removeHost } = hostsSlice.actions

export default hostsSlice.reducer;
