import React from "react";
import FullCalendar from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";
import timeGridPlugin from "@fullcalendar/timegrid";
import interactionPlugin from "@fullcalendar/interaction";
import viLocale from "@fullcalendar/core/locales/vi";
import { getMyCalendar, checkIn, checkOut } from "~/services/AttendanceService";
import { useState, useEffect } from "react";
import Detail from "./Detail";

const MyCalendar = () => {
  const [attendances, setAttendances] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [details, setDetails] = useState({});

  useEffect(() => {
    const fetchData = async () => {
      const data = await getMyCalendar();
      setAttendances(data);
    };
    fetchData();
  }, []);

  const showDetail = (detail) => {
    setDetails(detail);
    setShowForm(true);
  };

  const events = (attendances || []).map((a) => ({
    title: `${a.team}`,
    start: `${a.date}T${a.timeStart}`,
    end: `${a.date}T${a.timeEnd}`,
    extendedProps: {
      status: a.status,
      date: a.date,
      timeStart: a.timeStart,
      timeEnd: a.timeEnd,
      checkIn: a.checkIn,
      checkOut: a.checkOut,
    },
  }));

  const updateAttendance = async (data) => {
    if (data) {
      setAttendances((prev) =>
        prev.map((attendance) =>
          attendance.date === data.date ? data : attendance
        )
      );
    }
  };

  return (
    <div style={{ padding: "20px", height: "100%" }}>
      <FullCalendar
        plugins={[dayGridPlugin, timeGridPlugin, interactionPlugin]}
        initialView="timeGridWeek"
        headerToolbar={{
          left: "prev,next today",
          center: "title",
          right: "checkInButton checkOutButton",
        }}
        titleFormat={() => "LỊCH LÀM VIỆC"}
        allDaySlot={false}
        events={events}
        locale={viLocale}
        height="100%"
        slotMinTime="05:00:00"
        slotMaxTime="19:00:00"
        eventColor="rgba(32, 152, 199, 0.7)"
        eventClick={(info) => {
          showDetail(info.event.extendedProps);
        }}
        customButtons={{
          checkInButton: {
            text: "Check-in",
            click: async () => {
              const data = await checkIn();
              updateAttendance(data);
            },
          },
          checkOutButton: {
            text: "Check-out",
            click: async () => {
              const data = await checkOut();
              updateAttendance(data);
            },
          },
        }}
      />

      {showForm && (
        <Detail details={details} onClose={() => setShowForm(false)} />
      )}
    </div>
  );
};

export default MyCalendar;