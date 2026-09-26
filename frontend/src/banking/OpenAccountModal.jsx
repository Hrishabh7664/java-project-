import React, { useState } from 'react';

const OpenAccountModal = ({ isOpen, onClose, onSubmitAccount, isSubmitting }) => {
  const [holderName, setHolderName] = useState('');
  const [accountType, setAccountType] = useState('Savings');
  const [initialDeposit, setInitialDeposit] = useState('500.00');
  const [error, setError] = useState('');

  if (!isOpen) return null;

  const handleSubmit = (e) => {
    e.preventDefault(); // Controlled form event prevention
    setError('');

    if (!holderName.trim()) {
      setError('Please provide the account holder name.');
      return;
    }

    const depositNum = parseFloat(initialDeposit);
    if (isNaN(depositNum) || depositNum < 0) {
      setError('Initial deposit cannot be negative.');
      return;
    }

    const accountData = {
      holderName: holderName.trim(),
      accountType,
      initialDeposit: depositNum
    };

    onSubmitAccount(accountData, () => {
      setHolderName('');
      setAccountType('Savings');
      setInitialDeposit('500.00');
      onClose();
    });
  };

  return (
    <div
      className="modal fade show d-block"
      tabIndex="-1"
      role="dialog"
      style={{ backgroundColor: 'rgba(15, 23, 42, 0.65)', backdropFilter: 'blur(4px)' }}
    >
      <div className="modal-dialog modal-dialog-centered" role="document">
        <div className="modal-content border-0 shadow-lg" style={{ borderRadius: '14px', overflow: 'hidden' }}>
          <div className="modal-header bg-primary text-white py-3">
            <h5 className="modal-title fw-bold">
              <i className="bi bi-bank me-2"></i> Open New Bank Account
            </h5>
            <button
              type="button"
              className="btn-close btn-close-white"
              aria-label="Close"
              onClick={onClose}
              disabled={isSubmitting}
            ></button>
          </div>

          <form onSubmit={handleSubmit} noValidate>
            <div className="modal-body p-4">
              {error && (
                <div className="alert alert-danger py-2 small mb-3">
                  <i className="bi bi-exclamation-triangle-fill me-1"></i> {error}
                </div>
              )}

              <div className="mb-3">
                <label htmlFor="holderNameInput" className="form-label small fw-semibold">
                  Account Holder Full Name
                </label>
                <input
                  id="holderNameInput"
                  type="text"
                  className="form-control"
                  placeholder="e.g. Anurag Yadav"
                  value={holderName}
                  onChange={(e) => setHolderName(e.target.value)}
                  disabled={isSubmitting}
                  required
                />
              </div>

              <div className="mb-3">
                <label htmlFor="accountTypeSelect" className="form-label small fw-semibold">
                  Account Type
                </label>
                <select
                  id="accountTypeSelect"
                  className="form-select"
                  value={accountType}
                  onChange={(e) => setAccountType(e.target.value)}
                  disabled={isSubmitting}
                >
                  <option value="Savings">Savings Account (Standard 3.5% APY)</option>
                  <option value="Checking">Checking Account (Daily Operations)</option>
                  <option value="Business">Business Corporate Account</option>
                </select>
              </div>

              <div className="mb-3">
                <label htmlFor="depositInput" className="form-label small fw-semibold">
                  Initial Opening Deposit ($ USD)
                </label>
                <div className="input-group">
                  <span className="input-group-text bg-light">$</span>
                  <input
                    id="depositInput"
                    type="number"
                    step="0.01"
                    min="0"
                    className="form-control"
                    placeholder="500.00"
                    value={initialDeposit}
                    onChange={(e) => setInitialDeposit(e.target.value)}
                    disabled={isSubmitting}
                    required
                  />
                </div>
              </div>
            </div>

            <div className="modal-footer bg-light py-3">
              <button
                type="button"
                className="btn btn-outline-secondary rounded-pill px-4"
                onClick={onClose}
                disabled={isSubmitting}
              >
                Cancel
              </button>
              <button
                type="submit"
                className="btn btn-primary rounded-pill px-4 shadow-sm"
                disabled={isSubmitting}
              >
                {isSubmitting ? (
                  <>
                    <span className="spinner-border spinner-border-sm me-2" role="status"></span>
                    Creating...
                  </>
                ) : (
                  'Create Account'
                )}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default OpenAccountModal;
