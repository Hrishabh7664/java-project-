import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import StatCards from './StatCards';
import TransactionTable from './TransactionTable';
import TransferForm from './TransferForm';
import OpenAccountModal from './OpenAccountModal';
import {
  fetchStats,
  fetchTransactions,
  fetchAccounts,
  postTransfer,
  postNewAccount,
  logout,
  getCurrentUser
} from '../services/apiService';

const BankingDashboard = () => {
  const navigate = useNavigate();
  const currentUser = getCurrentUser();

  // Lifted state management (Experiment 3 Core Architecture)
  const [stats, setStats] = useState({
    totalBalance: 0,
    monthlyIncome: 0,
    monthlyExpenses: 0,
    activeAccounts: 0
  });
  const [transactions, setTransactions] = useState([]);
  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [alert, setAlert] = useState(null);

  // Helper to trigger timed user notifications
  const notify = (message, type = 'success') => {
    setAlert({ message, type });
    setTimeout(() => setAlert(null), 5000);
  };

  // Load initial dataset from Spring Boot REST service
  const loadDashboardData = async () => {
    try {
      setLoading(true);
      const [statsData, txData, accData] = await Promise.all([
        fetchStats(),
        fetchTransactions(),
        fetchAccounts()
      ]);
      setStats(statsData);
      setTransactions(txData);
      setAccounts(accData);
    } catch (err) {
      console.error('Error fetching banking data:', err);
      notify('Failed to load dashboard data. Ensure backend is running.', 'danger');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDashboardData();
  }, []);

  // State lifting callback: Execute funds transfer via apiService
  const handleTransfer = async (transferData, onSuccessCallback) => {
    try {
      setIsSubmitting(true);
      const newTx = await postTransfer(transferData);

      // Prepend newly created transaction to transaction list (Lifted state update)
      setTransactions((prev) => [newTx, ...prev]);

      // Re-fetch stats and accounts to reflect updated balances
      const [updatedStats, updatedAccounts] = await Promise.all([
        fetchStats(),
        fetchAccounts()
      ]);
      setStats(updatedStats);
      setAccounts(updatedAccounts);

      notify(`Transfer of $${transferData.amount.toFixed(2)} completed successfully!`, 'success');
      if (onSuccessCallback) onSuccessCallback();
    } catch (err) {
      console.error('Transfer failed:', err);
      const msg = err.response?.data?.message || err.message || 'Transfer failed. Check balance or recipient.';
      notify(msg, 'danger');
    } finally {
      setIsSubmitting(false);
    }
  };

  // State lifting callback: Open new account via apiService
  const handleCreateAccount = async (accountData, onSuccessCallback) => {
    try {
      setIsSubmitting(true);
      const newAcc = await postNewAccount(accountData);

      // Add to accounts list
      setAccounts((prev) => [...prev, newAcc]);

      // Re-fetch stats
      const updatedStats = await fetchStats();
      setStats(updatedStats);

      notify(`Account #${newAcc.accountNumber} created for ${newAcc.holderName}!`, 'success');
      if (onSuccessCallback) onSuccessCallback();
    } catch (err) {
      console.error('Account creation failed:', err);
      const msg = err.response?.data?.message || err.message || 'Failed to create account.';
      notify(msg, 'danger');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="d-flex flex-column min-vh-100" style={{ backgroundColor: '#f8fafc' }}>
      {/* Banking Navbar */}
      <header className="sticky-top shadow-sm bg-white">
        <nav className="navbar navbar-expand-lg navbar-light container py-3">
          <div className="d-flex align-items-center">
            <i className="bi bi-bank2 fs-3 text-primary me-2"></i>
            <span className="navbar-brand fw-bold mb-0 text-dark">
              Apex Global Bank <span className="badge bg-primary-subtle text-primary fs-6 ms-2">Exp 03 & 06</span>
            </span>
          </div>

          <div className="ms-auto d-flex align-items-center gap-3">
            <Link to="/" className="btn btn-sm btn-outline-secondary rounded-pill px-3">
              <i className="bi bi-arrow-left me-1"></i> Campus Connect
            </Link>

            <button
              type="button"
              className="btn btn-primary rounded-pill px-4 shadow-sm"
              onClick={() => setIsModalOpen(true)}
            >
              <i className="bi bi-plus-circle me-1"></i> Open New Account
            </button>

            <div className="dropdown">
              <button
                className="btn btn-light rounded-pill border d-flex align-items-center gap-2 px-3 py-1 dropdown-toggle"
                type="button"
                id="userMenuButton"
                data-bs-toggle="dropdown"
                aria-expanded="false"
              >
                <div
                  className="rounded-circle bg-primary text-white d-flex align-items-center justify-content-center fw-bold"
                  style={{ width: '28px', height: '28px', fontSize: '0.8rem' }}
                >
                  {currentUser?.username ? currentUser.username.charAt(0).toUpperCase() : 'U'}
                </div>
                <span className="small fw-semibold">{currentUser?.username || 'User'}</span>
              </button>
              <ul className="dropdown-menu dropdown-menu-end shadow-sm border-0" aria-labelledby="userMenuButton">
                <li><span className="dropdown-item-text small text-muted">Role: {currentUser?.role || 'ROLE_USER'}</span></li>
                <li><hr className="dropdown-divider" /></li>
                <li>
                  <button className="dropdown-item text-danger" onClick={handleLogout}>
                    <i className="bi bi-box-arrow-right me-2"></i> Sign Out
                  </button>
                </li>
              </ul>
            </div>
          </div>
        </nav>
      </header>

      {/* Main Content Area */}
      <main className="flex-grow-1 py-4 container">
        {/* Dynamic Alert Banner */}
        {alert && (
          <div className={`alert alert-${alert.type} alert-dismissible fade show shadow-sm mb-4`} role="alert">
            <i className={`bi ${alert.type === 'success' ? 'bi-check-circle-fill' : 'bi-exclamation-triangle-fill'} me-2`}></i>
            {alert.message}
            <button type="button" className="btn-close" aria-label="Close" onClick={() => setAlert(null)}></button>
          </div>
        )}

        {/* Dashboard Title & Quick Stats */}
        <div className="d-flex justify-content-between align-items-center mb-4">
          <div>
            <h2 className="h4 fw-bold mb-1 text-dark">Enterprise Banking Dashboard</h2>
            <p className="text-muted small mb-0">
              State lifted in React container &middot; Authenticated via JWT Bearer Token
            </p>
          </div>
          <button
            className="btn btn-outline-primary btn-sm rounded-pill px-3"
            onClick={loadDashboardData}
            disabled={loading}
          >
            <i className={`bi bi-arrow-clockwise me-1 ${loading ? 'spin' : ''}`}></i> Refresh Ledger
          </button>
        </div>

        {/* Component 1: StatCards (Receives lifted state as props) */}
        <StatCards stats={stats} />

        {/* Component 2 & 3: TransferForm & TransactionTable */}
        <div className="row g-4">
          <div className="col-12 col-lg-5">
            <TransferForm
              accounts={accounts}
              onSubmitTransfer={handleTransfer}
              isSubmitting={isSubmitting}
            />
          </div>
          <div className="col-12 col-lg-7">
            <TransactionTable transactions={transactions} />
          </div>
        </div>
      </main>

      {/* Component 4: OpenAccountModal (Receives open/close state & callback) */}
      <OpenAccountModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSubmitAccount={handleCreateAccount}
        isSubmitting={isSubmitting}
      />

      {/* Sticky Footer */}
      <footer className="mt-auto py-3 bg-white border-top text-center text-muted small">
        <div className="container">
          &copy; 2026 Apex Global Banking Portal &middot; Full Stack Java Programming Lab Experiments 01&ndash;06
        </div>
      </footer>
    </div>
  );
};

export default BankingDashboard;
