const HOST = "localhost";

const SPRING = `http://${HOST}:9001/api`;
const DW = `http://${HOST}:9002/api`;
const SERVLET = `http://${HOST}:9000/servlet-legacy`;

const clearLocalCookie = () => {
	document.cookie = "user_session=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;";
};


async function get(url) {
	const r = await fetch(url, { credentials: 'include' });

	if (r.status === 401) {
		clearLocalCookie();
		if (url.includes('/profile') || url.includes('/session')) {
			return { loggedIn: false, username: "Гість" };
		}
		const text = await r.text();
		throw new Error(`[401] Не авторизовано: ${text}`);
	}

	if (!r.ok) {
		const text = await r.text();
		throw new Error(`[${r.status}] ${text}`);
	}
	return r.json();
}

async function post(url, body) {
	const r = await fetch(url, {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		credentials: 'include',
		body: JSON.stringify(body),
	});
	if (!r.ok) {
		const text = await r.text();
		throw new Error(`[${r.status}] ${text}`);
	}
	return r.json();
}

async function postForm(url, params) {
	const r = await fetch(url, {
		method: 'POST',
		headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
		credentials: 'include',
		body: new URLSearchParams(params).toString(),
	});

	const text = await r.text();
	let json = {};
	try {
		json = text ? JSON.parse(text) : {};
	} catch {
		json = { error: text };
	}

	if (!r.ok) {
		throw new Error(json.error || `[${r.status}] Помилка сервера`);
	}
	return json;
}

async function del(url) {
	const r = await fetch(url, { method: 'DELETE', credentials: 'include' });
	if (!r.ok) {
		const text = await r.text();
		throw new Error(`[${r.status}] ${text}`);
	}
}

async function put(url, body) {
	const r = await fetch(url, {
		method: 'PUT',
		headers: { 'Content-Type': 'application/json' },
		credentials: 'include',
		body: body ? JSON.stringify(body) : undefined,
	});
	if (!r.ok) {
		const text = await r.text();
		throw new Error(`[${r.status}] ${text}`);
	}
	const text = await r.text();
	return text ? JSON.parse(text) : null;
}

export const api = {
	notes: {
		getAll: (userId) => get(`${SPRING}/notes?userId=${userId}`),
		create: (note) => post(`${SPRING}/notes`, note),
		update: (id, n) => put(`${SPRING}/notes/${id}`, n),
		remove: (id) => del(`${SPRING}/notes/${id}`),
	},
	users: {
		getAll: () => get(`${SPRING}/users`),
		create: (user) => post(`${SPRING}/users`, user),
		remove: (id) => del(`${SPRING}/users/${id}`),
	},
	wishes: {
		getAll: (userId) => get(`${DW}/wishes?userId=${userId}`),
		getStats: () => get(`${DW}/wishes/stats`),
		create: (wish) => post(`${DW}/wishes`, wish),
		achieve: (id) => put(`${DW}/wishes/${id}/achieve`),
		remove: (id) => del(`${DW}/wishes/${id}`),
	},
	session: {
		login: (u, p) => postForm(`${SERVLET}/login`, { username: u, password: p }),
		profile: () => get(`${SERVLET}/profile`),
		get: () => get(`${SERVLET}/profile`),
		logout: async () => {
			try {
				await postForm(`${SERVLET}/logout`, {});
			} catch (e) {
				console.warn('logout error:', e);
			} finally {
				clearLocalCookie();
				localStorage.clear();
				sessionStorage.clear();
				window.location.replace("login.html");
			}
		}
	},
};
