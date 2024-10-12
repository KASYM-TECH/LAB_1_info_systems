import { legacy_createStore} from "redux";


export const ActionType = {
    SET: 'set',
    ADD: 'add',
    CLEAR: 'clear',
}

const pointStore = legacy_createStore((state, action) => {
    console.log(state, action);
    switch (action.type) {
        case ActionType.SET:
            return action.payload;
        case ActionType.ADD:
            return [...state, action.payload];
        case ActionType.CLEAR:
            return [];
        default:
            return state;
    }
}, []);

export default pointStore;