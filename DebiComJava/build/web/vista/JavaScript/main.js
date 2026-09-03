const modal = document.getElementById('authModal');
    const openModalBtn = document.getElementById('openModalBtn');
    const closeModalBtn = document.getElementById('closeModalBtn');

    // Show/Hide Modal
    openModalBtn.addEventListener('click', () => modal.classList.add('active'));
    closeModalBtn.addEventListener('click', () => modal.classList.remove('active'));

    window.addEventListener('click', (e) => {
      if (e.target === modal) modal.classList.remove('active');
    });

    // Switch Tabs inside Modal
    function switchTab(tabId) {
      document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));
      document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));

      document.getElementById(tabId).classList.add('active');
      event.currentTarget.classList.add('active');
    }