import {Expression, GraphingCalculator, useCalculator} from "desmos-react/index";
import "./Graph.css";
import {useEffect} from "react";
import {AddPoint} from "../api/PointsApi";
import {COOKIE_TOKEN} from "../App";
import {useCookies} from "react-cookie";
import {useNavigate} from "react-router-dom";

export default function Graph({ points, r }){
    const [cookies, setCookie, removeCookie] = useCookies(['token']);
    const navigate = useNavigate();

    const invert = r < 0;

    const rectColor = randomRGBA(1, 50);

    return (
        <div className="calculator-container">
            <div className="calculator" style={{position: "relative"}}>
            <div style={{position: "absolute", top: 0, left: 0, zIndex: "9999", width: "100%", height: "100%" }}
                onClick={async e => {
                    const rect = e.target.getBoundingClientRect();
                    const vx = rect.width / 10, vy = rect.height / 10
                    const x = +((e.clientX - rect.left - rect.width / 2) / vx).toFixed(5);
                    const y = +(-(e.clientY - rect.top - rect.height / 2) / vy).toFixed(5);

                    console.log(x, y, r);

                    const status = await AddPoint({x: x, y: y, r: r});
                    if(status !== 200) {
                        if(status === 460) {
                            alert("Ошибка добавления точки")
                        }else {
                            removeCookie(COOKIE_TOKEN);
                            navigate('/');
                        }
                    }
                }}>
            </div>
            <GraphingCalculator attributes={{ className: "calculator" }}
                                fontSize={18}
                                expressions={false}
                                settingsMenu={false}
                                lockViewport
                                xAxisStep={1}
                                yAxisStep={1}
                                // projectorMode
            >
                <Sizer/>
                <Expression id="arc" latex={`x^{2}+y^{2}\\le ${r * r}\\ \\left\\{x\\${invert ? "g" : "l"}e0\\ \\right\\}\\left\\{y\\${invert ? "l" : "g"}e0\\right\\}`} color={randomRGBA(1, 50)}/>
                <Expression id="rect" latex={`y\\${invert ? "g" : "l"}e ${r}\\ \\left\\{0\\${invert ? "g" : "l"}e x\\${invert ? "g" : "l"}e ${r}\\right\\}\\left\\{y\\${invert ? "l" : "g"}e0\\right\\}`} color={rectColor}/>
                <Expression id="triangle" latex={`y\\${invert ? "l" : "g"}e x+${-r}\\left\\{y\\${invert ? "g" : "l"}e0\\right\\}\\ \\left\\{x\\${invert ? "l" : "g"}e0\\right\\}`} color={randomRGBA(1, 50)}/>
                <Expression id="rect_line" latex={`x = ${r}\\ \\left\\{0\\${invert ? "g" : "l"}e y\\${invert ? "g" : "l"}e${r}\\right\\}`} color={rectColor}/>

                {points && points.map((e, i) =>
                    <Expression id={"point" + i} latex={`(${e.x}, ${e.y})`} key={i} color={e.result ? "green" : "red"}/>
                )}
            </GraphingCalculator>
                </div>
        </div>
    )
}

function Sizer() {
    const calculator = useCalculator();

    useEffect(() => {
        console.log(calculator);
        if(calculator) calculator.setMathBounds({
            left: -5,
            right: 5,
            bottom: -5,
            top: 5
        });
    }, [calculator])


    return (
        <></>
    )
}

export function randomRGBA(alpha: number, darkness = 0): string {
    const r = Math.floor(Math.random() * (256 - darkness));
    const g = Math.floor(Math.random() * (256 - darkness));
    const b = Math.floor(Math.random() * (256 - darkness));
    return `rgba(${r}, ${g}, ${b}, ${alpha})`;
}