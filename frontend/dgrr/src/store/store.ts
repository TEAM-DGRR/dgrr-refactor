import { combineReducers, configureStore } from "@reduxjs/toolkit";
import gameSlice from "./gameSlice";

const reducer = combineReducers({
  game:gameSlice,
})

export const store = configureStore({
  reducer,
  middleware: (getDefaultMiddleware) => getDefaultMiddleware({
    serializableCheck: false
  })
})

export default store;
export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;