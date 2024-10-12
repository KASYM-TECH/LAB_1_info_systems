import {useState} from "react";
import "./Table.css";


export default function Table({ points}){
    return (
        <div className="table-container">
            <table className="dots-table">
                <thead>
                <tr>
                    <th>x</th>
                    <th>y</th>
                    <th>r</th>
                    <th>Дата и время</th>
                    <th>Время выполнения, ms</th>
                </tr>
                </thead>
                <tbody>
                {points.map((e, i) => {
                    return (
                        <tr key={i} style={{color: e.result ? "#00c905" : "red"}}>
                            <td>{e.x.toFixed(3)}</td>
                            <td>{e.y.toFixed(3)}</td>
                            <td>{e.r.toFixed(3)}</td>
                            <td>{e.time}</td>
                            <td>{e.execTime}</td>
                        </tr>
                    )
                })}
                <tr className="points-table-filler">
                    <td style={{height: "auto"}} colSpan={5}></td>
                </tr>
                </tbody>
            </table>
        </div>
    )
}