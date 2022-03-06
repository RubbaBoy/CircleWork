const REQUEST_URL = 'http://localhost:8080/api/'

export function fetchAuthed(path: string, init?: RequestInit | undefined): Promise<Response> {
    return fetchApi(path, appendHeaders({
        'token': sessionStorage.getItem('token') ?? ''
    }, init))
}

export function fetchApi(path: string, init?: RequestInit | undefined): Promise<Response> {
    let headers: HeadersInit = init?.method == 'POST' ? {
        'Content-Type': 'application/json'
    } : {}

    return fetch(REQUEST_URL + path, appendHeaders(headers, init))
}

function appendHeaders(headers: HeadersInit, init?: RequestInit | undefined): RequestInit {
    if (init == undefined) {
        return {
            headers: headers
        }
    }

    return {
        ...init,
        headers: {
            ...headers,
            ...init?.headers
        }
    }
}
