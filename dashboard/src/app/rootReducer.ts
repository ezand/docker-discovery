import { combineReducers } from '@reduxjs/toolkit'

import hostsReducer from '../features/docker/hosts/hostsSlice'

const rootReducer = combineReducers({
    hosts: hostsReducer
})

export type RootState = ReturnType<typeof rootReducer>

export default rootReducer
