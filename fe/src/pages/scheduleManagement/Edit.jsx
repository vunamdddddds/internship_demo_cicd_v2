import { X } from "lucide-react";
import { useEffect, useRef, useState } from "react";
import FullCalendar from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";
import timeGridPlugin from "@fullcalendar/timegrid";
import interactionPlugin from "@fullcalendar/interaction";
import viLocale from "@fullcalendar/core/locales/vi";
import { getSchedule } from "~/services/WorkScheduleService";
import ScheduleEdit from "./ScheduleEdit";
import ScheduleCreate from "./ScheduleCreate";

const Edit = ({ onClose, teamSelect }) => {
  const calendarRef = useRef(null);
  const [schedules, setSchedules] = useState([]);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [showEditForm, setShowEditForm] = useState(false);
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [newEventInfo, setNewEventInfo] = useState(null);
  const [isChanged, setIsChanged] = useState(false);

  useEffect(() => {
    // Đợi modal render xong rồi cập nhật lại kích thước FullCalendar
    const timer = setTimeout(() => {
      if (calendarRef.current) {
        const api = calendarRef.current.getApi();
        api.updateSize();
      }
    }, 200); // delay nhẹ để modal hiển thị ổn định (200ms là hợp lý)

    return () => clearTimeout(timer);
  }, []);

  useEffect(() => {
    const fetchData = async () => {
      const data = await getSchedule(teamSelect);
      setSchedules(data);
    };
    fetchData();
  }, []);

  const handleUpdate = async (data) => {
    setSchedules((prev) =>
      prev.map((schedule) => (schedule.id === data.id ? data : schedule))
    );
  };

  const handleDelete = async (id) => {
    setSchedules((prev) => prev.filter((item) => item.id !== Number(id)));
  };

  const handleAdd = async (data) => {
    setSchedules((prev) => [...prev, data]);
  };

  const getDateOfWeek = (dayName) => {
    const dayMap = {
      MONDAY: 1,
      TUESDAY: 2,
      WEDNESDAY: 3,
      THURSDAY: 4,
      FRIDAY: 5,
      SATURDAY: 6,
      SUNDAY: 7,
    };

    const today = new Date();
    const currentDay = today.getDay() || 7; // JS: Sunday=0 → đổi thành 7
    const monday = new Date(today);
    monday.setDate(today.getDate() - currentDay + 1);
    const target = new Date(monday);
    target.setDate(monday.getDate() + dayMap[dayName] - 1);
    return target.toISOString().split("T")[0];
  };

  const events = schedules.map((item) => ({
    id: item.id,
    start: `${getDateOfWeek(item.dayOfWeek)}T${item.timeStart}`,
    end: `${getDateOfWeek(item.dayOfWeek)}T${item.timeEnd}`,
  }));

  return (
    <div className="modal-overlay">
      <div className="modal-detail">
        <div className="modal-header">
          <h3>Sửa lịch làm việc nhóm</h3>
          <button className="modal-close" onClick={() => onClose(isChanged)}>
            <X size={20} />
          </button>
        </div>

        <FullCalendar
          ref={calendarRef}
          firstDay={1}
          plugins={[dayGridPlugin, timeGridPlugin, interactionPlugin]}
          initialView="timeGridWeek"
          headerToolbar={false}
          allDaySlot={false}
          locale={viLocale}
          height="90%"
          slotMinTime="05:00:00"
          slotMaxTime="19:00:00"
          eventColor="rgba(32, 152, 199, 0.7)"
          eventClick={(info) => {
            const event = info.event;
            const formatTime = (date) => {
              const hours = String(date.getHours()).padStart(2, "0");
              const minutes = String(date.getMinutes()).padStart(2, "0");
              return `${hours}:${minutes}`;
            };

            setSelectedEvent({
              id: event.id,
              timeStart: formatTime(event.start),
              timeEnd: formatTime(event.end),
            });

            setShowEditForm(true);
          }}
          events={events}
          dayHeaderFormat={{ weekday: "long" }}
          selectable={true}
          selectMirror={true}
          select={(selectInfo) => {
            const formatTime = (date) => {
              const hours = String(date.getHours()).padStart(2, "0");
              const minutes = String(date.getMinutes()).padStart(2, "0");
              return `${hours}:${minutes}`;
            };

            const dayNames = [
              "SUNDAY",
              "MONDAY",
              "TUESDAY",
              "WEDNESDAY",
              "THURSDAY",
              "FRIDAY",
              "SATURDAY",
            ];

            const dayOfWeek = dayNames[selectInfo.start.getDay()];

            setNewEventInfo({
              idTeam: teamSelect,
              dayOfWeek: dayOfWeek,
              timeStart: formatTime(selectInfo.start),
              timeEnd: selectInfo.end ? formatTime(selectInfo.end) : "",
            });

            setShowCreateForm(true);
          }}
          selectAllow={(selectInfo) => {
            const start = selectInfo.start;
            const end = selectInfo.end;

            // chỉ cho phép nếu cùng 1 ngày
            const isSameDay =
              start.getFullYear() === end.getFullYear() &&
              start.getMonth() === end.getMonth() &&
              start.getDate() === end.getDate();

            if (!isSameDay) return false;

            // kiểm tra xem ngày đó có event nào chưa
            const hasEvent = events.some((e) => {
              const eventStart = new Date(e.start);
              return (
                eventStart.getFullYear() === start.getFullYear() &&
                eventStart.getMonth() === start.getMonth() &&
                eventStart.getDate() === start.getDate()
              );
            });

            if (hasEvent) return false;

            return true;
          }}
        />
        <style>
          {`
            .fc-day-today {
             background-color: transparent !important;
             }
            `}
        </style>
      </div>

      {showEditForm && (
        <ScheduleEdit
          onClose={() => setShowEditForm(false)}
          selectedEvent={selectedEvent}
          handleUpdate={handleUpdate}
          handleDelete={handleDelete}
          setIsChanged={setIsChanged}
        />
      )}

      {showCreateForm && (
        <ScheduleCreate
          onClose={() => setShowCreateForm(false)}
          newEventInfo={newEventInfo}
          handleAdd={handleAdd}
          setIsChanged={setIsChanged}
        />
      )}
    </div>
  );
};

export default Edit;
