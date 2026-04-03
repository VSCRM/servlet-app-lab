export function toast(msg, type = 'info') {
	let el = document.getElementById('toast');
	if (!el) {
		el = document.createElement('div');
		el.id = 'toast';
		document.body.appendChild(el);
	}
	el.textContent = msg;
	el.className = `toast toast--${type} toast--show`;
	clearTimeout(el._t);
	el._t = setTimeout(() => el.classList.remove('toast--show'), 3000);
}
