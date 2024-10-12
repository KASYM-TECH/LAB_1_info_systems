import belle from "belle";
import {useState} from "react";
import "./Form.css";
import {AddPoint, ClearPoints} from "../api/PointsApi";
import {useCookies} from "react-cookie";
import {useNavigate} from "react-router-dom";
import {COOKIE_TOKEN} from "../App";


export default function Form({setR}) {
    const [form, setForm] = useState({x: 0, y: 0, r: 0});
    const [cookies, setCookie, removeCookie] = useCookies(['token']);
    const navigate = useNavigate();

    return (
        <form className="form" onSubmit={async e => {
            e.preventDefault();

            console.log(form);

            const status = await AddPoint(form);
            if(status !== 200) {
                if(status === 460) {
                    alert("Ошибка добавления точки")
                }else {
                    removeCookie(COOKIE_TOKEN);
                    navigate('/');
                }
            }
        }}>
            <div className="inputs">
                <div>
                    <p>X:</p>
                    <belle.Select wrapperStyle={{background: "#FFFC"}} defaultValue="0" onUpdate={e => setForm({...form, x: +e.value})}>
                        <belle.Option value="4">4</belle.Option>
                        <belle.Option value="3">3</belle.Option>
                        <belle.Option value="2">2</belle.Option>
                        <belle.Option value="1">1</belle.Option>
                        <belle.Option value="0">0</belle.Option>
                        <belle.Option value="-1">-1</belle.Option>
                        <belle.Option value="-2">-2</belle.Option>
                        <belle.Option value="-3">-3</belle.Option>
                        <belle.Option value="-4">-4</belle.Option>
                    </belle.Select>
                </div>
                <div>
                    <p>Y:</p>
                    -5 <input type="range" min="-5" max="3" step="0.1"
                       defaultValue={0} onInput={e => setForm({...form, y: +e.target.value})}/> 3
                </div>
                <div>
                    <p>R:</p>
                <belle.Select wrapperStyle={{background: "#FFFC"}} onUpdate={e => {
                    setForm({...form, r: +e.value});
                    setR(+e.value)
                }} defaultValue="0">
                    <belle.Option value="4">4</belle.Option>
                    <belle.Option value="3">3</belle.Option>
                    <belle.Option value="2">2</belle.Option>
                    <belle.Option value="1">1</belle.Option>
                    <belle.Option value="0">0</belle.Option>
                    <belle.Option value="-1">-1</belle.Option>
                    <belle.Option value="-2">-2</belle.Option>
                    <belle.Option value="-3">-3</belle.Option>
                    <belle.Option value="-4">-4</belle.Option>
                </belle.Select>
                </div>
            </div>
            <div className="controls">
                <input type="button" value="Очистить" onClick={async e => {
                    if(await ClearPoints() !== 200){
                        removeCookie(COOKIE_TOKEN);
                        navigate('/');
                    }
                } }/>
                <input type="submit" value="Отправить"/>
                <input type="button" value="На главную" onClick={e => {
                    removeCookie(COOKIE_TOKEN);
                    navigate('/');
                }}/>
            </div>
        </form>
    )
}