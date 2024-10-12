import {useSelector} from "react-redux";
import Graph from "../components/Graph";
import {useEffect, useState} from "react";
import Table from "../components/Table";
import Form from "../components/Form";
import {COOKIE_TOKEN} from "../App";
import styles from "./Main.module.css";
import {useCookies} from "react-cookie";
import {useNavigate} from "react-router-dom";
import {GetPoints} from "../api/PointsApi";



export default function Main(){
    const [r, setR] = useState(0);
    const points = useSelector((state) => {
        console.log(state);
        return state;
    });
    const [cookies, setCookie, removeCookie] = useCookies([COOKIE_TOKEN]);
    const navigate = useNavigate();


    useEffect(() => {
        (async () => {
            if(!cookies[COOKIE_TOKEN] || await GetPoints() !== 200) {
                removeCookie(COOKIE_TOKEN);
                navigate('/');
            }
        })()
    }, [])

    return (
        <div className={styles.main}>
            <Graph points={points} r={r}/>
            <Form setR={setR}/>
            <Table points={points}/>
        </div>
    )
}