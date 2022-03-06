import React from 'react'
import './MiddleContent.scss'
import {Col} from "react-bootstrap";

interface MiddleContentProps {
    children?: JSX.Element | JSX.Element[];
}

export const MiddleContent = (props: MiddleContentProps) => {
    return (
        <Col className="MiddleContent" xs={3}>
            {props.children}
        </Col>
    )
}
