import pointStore, {ActionType} from "../PointStore";
import {BACKEND_HOST_URL} from "../App";
import axios from "axios";


export async function AddPoint(point){
    point.time = "" + Date.now();
    try {
        const res = await axios.get(BACKEND_HOST_URL + '/api/dots/checkDot?' + new URLSearchParams(point), {
            withCredentials: true
        })
        pointStore.dispatch({type: ActionType.ADD, payload: res.data});
        return res.status;
    } catch (err) {
        console.log(err);
        return err.response ? err.response.status : 400;
    }

    /*pointStore.dispatch({type: ActionType.ADD, payload: {
            ...point,
            time: Date.now(),
            execTime: Math.random() * 100,
            result: Math.random() > 0.5
        }})*/
}

export async function GetPoints(){
    try {
        const res = await axios(BACKEND_HOST_URL + '/api/dots/getAllDots', {
            method: 'GET',
            withCredentials: true
        });
        console.log(res.data)
        pointStore.dispatch({type: ActionType.SET, payload: res.data});
        return res.status;
    } catch (e) {
        console.log(e);
        return e.response ? e.response.status : 400;
    }
}

export async function ClearPoints(){
    try {
        const res = await axios(BACKEND_HOST_URL + '/api/dots/clearDots', {
            method: 'GET',
            withCredentials: true
        });
        pointStore.dispatch({type: ActionType.CLEAR});
        return res.status;
    } catch (e) {
        console.log(e);
        return e.response ? e.response.status : 400;
    }
}