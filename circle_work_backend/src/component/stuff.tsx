import React, { Fragment } from 'react'
import {Col, Row} from "react-bootstrap";

export function someBody(leftBar: JSX.Element, content: JSX.Element) {
    return (
        <Row>
            <Col xs={2} className="left-col">
                {leftBar}
            </Col>
            <Col xs={12 - 4} className="center-col">
                {content}
            </Col>
            <Col xs={2} className="right-col">

            </Col>
        </Row>
    )
}