import {BACKEND_HOST_URL, COOKIE_TOKEN} from "../App";
import axios from "axios";
import {useNavigate} from "react-router-dom";
import styles from "./Auth.module.css";
import {useEffect, useState} from "react";
import {useCookies} from "react-cookie";
import {randomRGBA} from "../components/Graph";
import {GetPoints} from "../api/PointsApi";

const LOGIN_NAME = 'login';
const REGISTER_NAME = 'register';

const ERRORS = {
    470: "Пользователь с таким логином уже существует",
    471: "Пользователя не существует",
    472: "Неверный пароль"
}


export default function Auth() {
    const navigate = useNavigate();
    const [error, setError] = useState("");
    const [cookies, setCookie, removeCookie] = useCookies([COOKIE_TOKEN]);

    useEffect(() => {
        if(cookies[COOKIE_TOKEN]) navigate("/main");
    }, [])

    function SendAuthRequest(type: 'login' | 'register', data){
        axios.get(
            BACKEND_HOST_URL + "/api/auth/"
            + type + "?"
            + new URLSearchParams(data)
        ).then(async (res) => {
            console.log(res);
            if (res.data.jwtToken) {
                removeCookie(COOKIE_TOKEN);
                setCookie(COOKIE_TOKEN, res.data.jwtToken);
                if (await GetPoints() === 200) {
                    navigate('/main');
                } else {
                    removeCookie(COOKIE_TOKEN);
                    setError("Токен невалидный");
                }
            } else if (type === 'register')
                SendAuthRequest('login', data);
            else
                setError("Неверный логин или пароль");

        }).catch((error) => {
            console.log(error);
            if(error.response) {
                setError(ERRORS[error.response.status]);
            }else {
                setError(error.message);
            }
        })
    }

    return (
        <div className={styles.main}>
            <div className={styles.header}>
                    <p style={{color: randomRGBA(1)}}>Мальков Павел</p>
                    <p style={{color: randomRGBA(1)}}>P3110</p>
                    <p style={{color: randomRGBA(1)}}>Вариант №1042</p>
            </div>

            <form className={styles.form} onSubmit={e => {
                e.preventDefault()

                console.log(e);

                SendAuthRequest(
                    e.nativeEvent.submitter.name === LOGIN_NAME ? "login" : "register",
                    Object.fromEntries(new FormData(e.target))
                )
            }}>
                <input type="text" name="username" placeholder="Логин" required onInput={LengthValidator}/>
                <input type="text" name="password" placeholder="Пароль" required onInput={LengthValidator}/>
                {error && <p className={styles.error}>{error}</p>}
                <input type="submit" name={LOGIN_NAME} value="Войти"/>
                <input type="submit" name={REGISTER_NAME} value="Зарегистрироваться"/>
            </form>
        </div>
    )
}

function LengthValidator(event){
    if(event.target.value.trim().length < 1){
        event.target.setCustomValidity("Введите хотя бы один символ (пробел не считается)");
        return;
    }
    event.target.setCustomValidity("");
}