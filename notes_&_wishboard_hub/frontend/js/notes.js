import { api } from './api.js';
import { toast } from './toast.js';

let currentUserId = null;

export async function initNotes() {
	try {
		const user = await api.session.profile();
		if (user.loggedIn && user.userId) {
			currentUserId = user.userId;
			await loadNotes();
		}
	} catch (err) {
		console.error(err);
	}

	document.getElementById('note-form')?.addEventListener('submit', async e => {
		e.preventDefault();
		const title = document.getElementById('note-title').value.trim();
		const content = document.getElementById('note-content').value.trim();

		if (!title || !content || !currentUserId) {
			toast('Заповніть поля', 'error');
			return;
		}

		const btn = e.submitter;
		btn.disabled = true;
		btn.textContent = 'Збереження…';

		try {
			await api.notes.create({ title, content, userId: currentUserId });
			e.target.reset();
			const detailsTag = e.target.closest('details');
			if (detailsTag) detailsTag.removeAttribute('open');
			toast('Збережено ✓', 'success');
			await loadNotes();
		} catch (err) {
			toast('Помилка: ' + err.message, 'error');
		} finally {
			btn.disabled = false;
			btn.textContent = 'Зберегти';
		}
	});

	document.getElementById('notes-list')?.addEventListener('click', async e => {
		const btn = e.target.closest('[data-action]');
		if (!btn) return;
		const id = Number(btn.dataset.id);

		if (btn.dataset.action === 'delete-note') {
			if (!confirm('Видалити нотатку?')) return;
			btn.disabled = true;
			try {
				await api.notes.remove(id);
				toast('Видалено', 'info');
				await loadNotes();
			} catch (err) {
				toast('Помилка: ' + err.message, 'error');
				btn.disabled = false;
			}
		}
	});
}

async function loadNotes() {
	const list = document.getElementById('notes-list');
	if (!list || !currentUserId) return;

	list.innerHTML = '<p class="loading">Завантаження…</p>';
	try {
		const notes = await api.notes.getAll(currentUserId);
		if (!notes.length) {
			list.innerHTML = '<p class="empty">Нотаток немає</p>';
			return;
		}
		list.innerHTML = notes.map(n => `
            <div class="note-card">
                <div class="note-card__header">
                    <strong>${esc(n.title)}</strong>
                    <button data-action="delete-note" data-id="${n.id}" class="btn btn-danger btn-sm">✕</button>
                </div>
                <p class="note-card__body">${esc(n.content)}</p>
            </div>
        `).join('');
	} catch (err) {
		list.innerHTML = `<p class="error">Помилка завантаження<br><small>${err.message}</small></p>`;
	}
}

function esc(s) {
	return String(s ?? '').replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}
