import React, {useState, useEffect, Fragment, useContext} from 'react';
import {GoalCategory} from "../../logic/objects";
import {fetchApi} from "../../logic/request-helper";
import {navSideButton} from "../home/Home";
import {Card, Col, Row} from "react-bootstrap";
import {someBody} from "../stuff";
import {useNavigate} from "react-router";
import './Categories.scss'
import {convertColor} from "../../logic/utilities";
import CategoriesContext from "../../logic/Category";


export const Categories = () => {
    const navigate = useNavigate()

    const categories = useContext(CategoriesContext)
    const[hovering, setHovering] = useState<GoalCategory | undefined>()

    return(
        <Fragment>
            <div className="side-buttons">
                {navSideButton('Dashboard', '/dashboard')}
            </div>

            <Row className="Categories mx-0">
                {someBody(
                    <div className="sidebar-incomplete upside-down">
                        <div className="bottom-round"></div>
                    </div>,
                    <Fragment>
                        <Row xs={12} className="first-row">
                                <h4>Categories</h4>
                                <p>here are our default categories :)</p>

                                <Row xs={12} className="list-row">
                                    {categories.map(category => {
                                        return <Col xs={3}>
                                            <Card className="category" onClick={() => navigate('/resources/' + category.id)} style={{...(hovering == category ? {boxShadow: `0 0px 10px #${convertColor(category.color)}`} : null)}} onMouseEnter={() => setHovering(category)} onMouseLeave={() => setHovering(undefined)}>
                                                <Card.Body>
                                                    <Card.Title className="m-0">
                                                        {category.name}
                                                    </Card.Title>
                                                    {category.description}
                                                </Card.Body>
                                            </Card>
                                        </Col>;
                                    })}
                                </Row>
                            </Row>

                    </Fragment>
                )}
            </Row>
        </Fragment>
    );

}