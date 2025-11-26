import React, { useState, useEffect } from 'react';
import AllowanceApi from '../../api/AllowanceApi';
import Pagination from '../../components/Pagination';
import MyAllowanceHistoryTable from './MyAllowanceHistoryTable'; // Import component mới

const MyAllowanceHistory = () => {
    const [allowances, setAllowances] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [totalAmountDisplay, setTotalAmountDisplay] = useState('0 VND'); // New state for total amount display
    
    const [paginationInfo, setPaginationInfo] = useState({
        totalElements: 0,
        totalPages: 1,
        hasNext: false,
        hasPrevious: false
    });
    
    const [filters, setFilters] = useState({
        page: 1,
        size: 10,
    });

    // Helper to format currency
    const formatCurrency = (amount) =>
        new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(amount);


    const fetchHistory = (pageToFetch) => {
        setLoading(true);
        setError(null);
        const apiParams = {
            page: pageToFetch - 1,
            size: filters.size,
            sort: 'paidAt,desc',
        };
        AllowanceApi.getMyHistory(apiParams)
            .then(response => {
                setAllowances(response.content);
                setPaginationInfo({
                    totalElements: response.totalElements,
                    totalPages: response.totalPages,
                    hasNext: response.hasNext,
                    hasPrevious: response.hasPrevious,
                });
            })
            .catch(err => {
                setError('Không thể tải dữ liệu. Vui lòng thử lại sau.');
                console.error(err);
            })
            .finally(() => {
                setLoading(false);
            });
    };

    useEffect(() => {
        fetchHistory(filters.page);
    }, [filters.page]);

    // Calculate total amount for displayed allowances whenever 'allowances' changes
    useEffect(() => {
        const sum = allowances.reduce((acc, current) => acc + current.amount, 0);
        setTotalAmountDisplay(formatCurrency(sum));
    }, [allowances]);

    const handlePageChange = (newPage) => {
        if (newPage > 0 && newPage <= paginationInfo.totalPages) {
            setFilters(prevFilters => ({ ...prevFilters, page: newPage }));
        }
    };

    return (
        <div className="main-content">
            <div className="page-title">Lịch sử Phụ cấp</div>
            <div className="text-xl font-bold mb-4">
                Tổng tiền hiển thị: <span className="text-green-600">{totalAmountDisplay}</span>
            </div>

            {error && <div className="text-center p-4 text-red-500">{error}</div>}
            
            <MyAllowanceHistoryTable data={allowances} loading={loading} />

            {!error && paginationInfo.totalElements > 0 && (
                 <Pagination
                    pagination={paginationInfo}
                    currentPage={filters.page}
                    changePage={handlePageChange}
                    name="bản ghi"
                />
            )}
        </div>
    );
};

export default MyAllowanceHistory;

