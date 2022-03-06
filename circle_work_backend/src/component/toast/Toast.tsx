import {Toast, ToastContainer} from "react-bootstrap";
import React from "react";
import './Toast.scss'

export interface SimpleToastProps {
    title: string
    color: string
    body: string
    show: boolean
    onHide: () => void
}

export const SimpleToast = (props: SimpleToastProps) => {
    return (
        <ToastContainer className="p-3" position="top-end">
            <Toast onClose={() => props.onHide()} show={props.show} animation={false}>
                <Toast.Header>
                    <div className="color me-2 rounded" style={{backgroundColor: `#${props.color}`}}></div>
                    <strong className="me-auto">{props.title}</strong>
                    {/*<small>11 mins ago</small>*/}
                </Toast.Header>
                <Toast.Body>{props.body}</Toast.Body>
            </Toast>
        </ToastContainer>
    )
}
